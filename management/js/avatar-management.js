document.addEventListener("DOMContentLoaded", function () {
  // 删除图片逻辑
  const deleteModal = document.getElementById("delete-modal");
  const confirmDelete = document.getElementById("confirm-delete");
  const cancelDelete = document.getElementById("cancel-delete");
  let currentAvatarItem = null;

  // 点击删除按钮时显示模态框
  const deleteOverlays = document.querySelectorAll(".delete-overlay");
  deleteOverlays.forEach((overlay) => {
    overlay.addEventListener("click", function (event) {
      event.stopPropagation(); // 阻止事件冒泡
      currentAvatarItem = this.closest(".avatar-item"); // 记录当前点击的头像
      deleteModal.style.display = "flex"; // 显示模态框
    });
  });

  // 确认删除
  confirmDelete.addEventListener("click", function () {
    if (currentAvatarItem) {
      currentAvatarItem.remove(); // 删除头像
      console.log("图片已删除");
    }
    deleteModal.style.display = "none"; // 隐藏模态框
  });

  // 取消删除
  cancelDelete.addEventListener("click", function () {
    deleteModal.style.display = "none"; // 隐藏模态框
  });

  // 上传图片逻辑
  const uploadInput = document.getElementById("upload-input");
  const uploadButton = document.querySelector(".upload-button");

  // 点击整个上传按钮区域触发文件选择
  uploadButton.addEventListener("click", function () {
    uploadInput.click();
  });

  uploadInput.addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (file && file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = function (e) {
        const avatarGrid = document.querySelector(".avatar-grid");
        const newAvatarItem = document.createElement("div");
        newAvatarItem.classList.add("avatar-item");
        newAvatarItem.innerHTML = `
                    <img src="${e.target.result}" alt="上传的头像">
                    <div class="delete-overlay">×</div>
                `;
        avatarGrid.insertBefore(newAvatarItem, uploadButton);
        console.log("图片已上传");
      };
      reader.readAsDataURL(file);
    } else {
      alert("请选择有效的图片文件！");
    }
  });
});
