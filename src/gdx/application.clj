(ns gdx.application
  (:require [clojure.application-listener :as application-listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.lwjgl3-application-configuration :as lwjgl3-config]))

(defn f!
  [{:keys [title windowed-mode foreground-fps listener]}]
  (lwjgl3-application/f (application-listener/new
                           (let [[f params] listener]
                             (f params)))
                          (doto (lwjgl3-config/new)
                            (lwjgl3-config/set-title! title)
                            (lwjgl3-config/set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
                            (lwjgl3-config/set-foreground-fps! foreground-fps))))
