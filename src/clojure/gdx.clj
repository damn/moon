(ns clojure.gdx
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn app [& args]
  (apply gdx/app args))
