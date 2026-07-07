(ns clojure.levelgen-test-application
  (:require [clojure.lwjgl3-application :as lwjgl3-application]
            clojure.levelgen-test-application-listener
            clojure.state))

(defn start!
  [{{:keys [title windowed-mode foreground-fps]} :lwjgl-app-config :as config}]
  (lwjgl3-application/f!
   {:title title
    :windowed-mode windowed-mode
    :foreground-fps foreground-fps}
   (clojure.levelgen-test-application-listener/f clojure.state/application
                                               config)))
