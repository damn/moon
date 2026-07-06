(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn new [{:keys [title windowed-mode foreground-fps]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
    (.setForegroundFPS foreground-fps)))
