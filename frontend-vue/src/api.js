// TODO FE-01~04: 공통 API 명세와 구현 체크리스트를 보고 완성하세요.
let session = { accessToken: "", user: null };
export const getSession = () => session;
export function clearSession() {
  session = { accessToken: "", user: null };
}

function authorizationHeaders() {
  // 👇 FE-02 완료: 메모리에 보관한 토큰을 Bearer 헤더로 보냅니다.
  if (session.accessToken) {
    return { Authorization: `Bearer ${session.accessToken}` };
  }
  return {};
}

export async function request(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...authorizationHeaders(),
      ...options.headers,
    },
  });
  const data = response.status === 204 ? null : await response.json();
  if (!response.ok) {
    // 👇 FE-03 완료: 401(인증 실패, 만료 등)이면 메모리의 로그인 상태도 해제합니다.
    if (response.status === 401) {
      clearSession();
    }

    const error = new Error(
        data?.message || data?.code || `HTTP ${response.status}`,
    );
    error.status = response.status;
    error.code = data?.code;
    throw error;
  }
  return data;
}

export async function login(email, password) {
  // 👇 FE-01 완료: POST /auth/login → 응답의 토큰과 사용자 정보를 session에 저장.
  const data = await request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });

  // 성공적으로 응답을 받으면 메모리(session)에 저장합니다.
  session.accessToken = data.accessToken;
  session.user = data.user;

  return data.user;
}

export function canEdit(post) {
  // 👇 FE-04 완료: 로그인 사용자 ID와 글 작성자 ID를 비교합니다.
  if (!session.user) return false; // 로그인 안 했으면 무조건 false
  return session.user.id === post.authorId;

}

export const api = {
  list: () => request("/posts"),
  detail: (id) => request(`/posts/${id}`),
  create: (input) =>
      request("/posts", { method: "POST", body: JSON.stringify(input) }),
  update: (id, input) =>
      request(`/posts/${id}`, { method: "PUT", body: JSON.stringify(input) }),
  remove: (id) => request(`/posts/${id}`, { method: "DELETE" }),
  me: () => request("/me"),
};