(ns moon.start.lwjgl-application
  (:require [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]))

(defn step
  [{:keys [app/config
           app/listener]}]
  (application/create listener config))
