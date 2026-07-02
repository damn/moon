(ns moon.application.start
  (:require [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.os.mac-os-x :as mac-os-x]
            [clojure.gdx.shared-library-loader.os :as os]
            [clojure.lwjgl.configuration.glfw-library-name :as glfw-library-name]
            [clojure.lwjgl.configuration.set! :as conf-set!])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn f!
  [{:keys [listener title windowed-mode foreground-fps]}]
  (when (= os/v mac-os-x/v)
    (conf-set!/f glfw-library-name/v "glfw_async"))
  (lwjgl3-application/f (let [[f params] listener]
                          (f params))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps))))
