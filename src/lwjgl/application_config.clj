(ns lwjgl.application-config
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(defn create
  [{:keys [title windowed-mode foreground-fps]}]
  (doto (config/create)
    (config/set-title! title)
    (config/set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
    (config/set-foreground-fps! foreground-fps)))
