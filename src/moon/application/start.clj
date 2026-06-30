(ns moon.application.start
  (:require [clojure.gdx :as gdx]))

(defn f!
  [{:keys [listener title windowed-mode foreground-fps]}]
  (gdx/macos-glfw-async!)
  (gdx/lwjgl3-application! (let [[f params] listener]
                             (f params))
                           (doto (gdx/lwjgl3-application-configuration)
                             (gdx/lwjgl3-config-set-title! title)
                             (gdx/lwjgl3-config-set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
                             (gdx/lwjgl3-config-set-foreground-fps! foreground-fps))))
