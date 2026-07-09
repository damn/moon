(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create [& args]
  (apply lwjgl3-application/create args))
