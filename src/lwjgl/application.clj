(ns lwjgl.application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create [listener config]
  (lwjgl3-application/create listener config))
