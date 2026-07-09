(ns gdx.lwjgl3-application-configuration
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(defn set-title! [& args]
  (apply lwjgl3-application-configuration/set-title! args))

(defn set-windowed-mode! [& args]
  (apply lwjgl3-application-configuration/set-windowed-mode! args))

(defn set-foreground-fps! [& args]
  (apply lwjgl3-application-configuration/set-foreground-fps! args))

(def k->opts
  lwjgl3-application-configuration/k->opts)

(defn build [& args]
  (apply lwjgl3-application-configuration/build args))
