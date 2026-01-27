(ns moon.ui.message
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn show! [^Actor this text]
  (.setUserObject this (atom {:text text
                              :counter 0})))
