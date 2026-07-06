(ns gdx.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-config]))

(defn f!
  [{:keys [title windowed-mode foreground-fps listener]}]
  (lwjgl3-application/new (application-listener/new
                           (let [[f params] listener]
                             (f params)))
                          (doto (lwjgl3-config/new)
                            (lwjgl3-config/set-title! title)
                            (lwjgl3-config/set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
                            (lwjgl3-config/set-foreground-fps! foreground-fps))))
