(ns levelgen-test.application
  (:require [clojure.application-listener :as application-listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.lwjgl3-application-configuration :as lwjgl3-config]
            levelgen-test.application-listener
            levelgen-test.state))

(defn start!
  [{{:keys [title windowed-mode foreground-fps]} :lwjgl-app-config :as config}]
  (lwjgl3-application/f (application-listener/new
                         (levelgen-test.application-listener/f levelgen-test.state/application
                                                               config))
                        (doto (lwjgl3-config/new)
                          (lwjgl3-config/set-title! title)
                          (lwjgl3-config/set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
                          (lwjgl3-config/set-foreground-fps! foreground-fps))))
