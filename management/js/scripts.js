document.addEventListener("DOMContentLoaded", function () {
  // 头像点击放大
  const avatars = document.querySelectorAll(".avatar");
  const avatarModal = document.getElementById("avatar-modal");
  const modalImg = document.getElementById("modal-avatar");
  const closeBtn = document.querySelector(".close");

  avatars.forEach((avatar) => {
    avatar.addEventListener("click", function () {
      avatarModal.style.display = "flex";
      modalImg.src = this.src;
    });
  });

  closeBtn.addEventListener("click", function () {
    avatarModal.style.display = "none";
  });

  // 重置头像模态框
  const resetModal = document.getElementById("reset-modal");
  const resetButtons = document.querySelectorAll(".reset-avatar");
  const confirmReset = document.getElementById("confirm-reset");
  const cancelReset = document.getElementById("cancel-reset");

  resetButtons.forEach((button) => {
    button.addEventListener("click", function () {
      resetModal.style.display = "flex";
    });
  });

  confirmReset.addEventListener("click", function () {
    resetModal.style.display = "none";
    console.log("头像已重置");
  });

  cancelReset.addEventListener("click", function () {
    resetModal.style.display = "none";
  });

  // 更新用户类型模态框
  const updateModal = document.getElementById("update-modal");
  const updateButtons = document.querySelectorAll(".update-type");
  const confirmUpdate = document.getElementById("confirm-update");
  const cancelUpdate = document.getElementById("cancel-update");
  const newUserType = document.getElementById("new-user-type");

  updateButtons.forEach((button) => {
    button.addEventListener("click", function () {
      updateModal.style.display = "flex";
    });
  });

  confirmUpdate.addEventListener("click", function () {
    const selectedType = newUserType.value;
    if (selectedType) {
      console.log("用户类型已更新为: " + selectedType);
      updateModal.style.display = "none";
    }
  });

  cancelUpdate.addEventListener("click", function () {
    updateModal.style.display = "none";
  });
});
