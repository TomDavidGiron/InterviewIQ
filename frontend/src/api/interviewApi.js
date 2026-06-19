import apiClient from "./client";

export async function getTopics() {
  const response = await apiClient.get("/api/interview/topics");
  return response.data;
}

export async function startInterview(payload) {
  const response = await apiClient.post("/api/interview/start", payload);
  return response.data;
}

export async function startJobSpecificInterview(payload) {
  const response = await apiClient.post("/api/interview/job-specific", payload);
  return response.data;
}

export async function submitAnswer(sessionId, payload) {
  const response = await apiClient.post(
    `/api/interview/${sessionId}/answer`,
    payload
  );
  return response.data;
}

export async function getSummary(sessionId) {
  const response = await apiClient.get(`/api/interview/${sessionId}/summary`);
  return response.data;
}

export async function getHistory(page = 0, size = 20) {
  const response = await apiClient.get("/api/interview/history", {
    params: { page, size }
  });
  return response.data;
}

export async function getSessionDetails(sessionId) {
  const response = await apiClient.get(`/api/interview/${sessionId}/details`);
  return response.data;
}

export async function getSkillGraphBySession(sessionId) {
  const response = await apiClient.get(
    `/api/interview/${sessionId}/skill-graph`
  );
  return response.data;
}

export async function getSkillGraphByUser(userId) {
  const response = await apiClient.get(
    `/api/interview/users/${userId}/skill-graph`
  );
  return response.data;
}