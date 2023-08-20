import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import styles from "./SidebarNavigation.module.css";

const SidebarNavigation = () => {
  const navigate = useNavigate();
  const [activeButtonIndex, setActiveButtonIndex] = useState(-1);

  const handleButtonClick = (index) => {
    if (activeButtonIndex === index) {
      return;
    }

    setActiveButtonIndex(index);

    if (index === 2) { // Проверяем, если нажата кнопка "Portfolio"
      navigate("/profile-page"); // Перенаправляем на указанный маршрут
    }
  };

  const mainButtons = [
    { text: "Home", style: "" },
    { text: "Transaction", style: styles.trans },
    { text: "Portfolio", style: styles.port },
    { text: "Wallet", style: styles.wallet },
  ];

  return (
    <div className={`${styles.navigation} ${styles.navigationSeparator}`}>
      {mainButtons.map((button, index) => (
        <div
          key={index}
          className={`${styles.wrapForElement} ${activeButtonIndex === index ? styles.clicked : ""
            }`}
        >
          <button
            className={`${styles.button} ${button.style}`}
            onClick={() => handleButtonClick(index)}
          >
            {button.text}
          </button>
        </div>
      ))}
    </div>
  );
};



export default SidebarNavigation;
