(ns gdl.math.vector3
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.vector3 :as vector3]))

(defn x [& args]
  (apply vector3/x args))

(defn y [& args]
  (apply vector3/y args))

(defn z [& args]
  (apply vector3/z args))

(defn set-x! [& args]
  (apply vector3/set-x! args))

(defn set-y! [& args]
  (apply vector3/set-y! args))

(defn clojurize [v3]
  [(x v3) (y v3) (z v3)])
