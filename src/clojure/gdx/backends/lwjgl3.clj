(ns clojure.gdx.backends.lwjgl3
  (:require [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application.config :as config]))

(def use-glfw-async! config/use-glfw-async!)

(def application! application/create)
