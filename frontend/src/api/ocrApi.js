import apiClient from "./client";

export async function extractTextFromImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await apiClient.post("/api/image-upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });

  return response.data;
}