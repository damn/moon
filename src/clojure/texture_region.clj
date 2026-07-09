(ns clojure.texture-region
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn get-region-height [& args]
  (apply texture-region/get-region-height args))

(defn get-region-width [& args]
  (apply texture-region/get-region-width args))

(defn get-texture [& args]
  (apply texture-region/get-texture args))

(defn get-u [& args]
  (apply texture-region/get-u args))

(defn get-u2 [& args]
  (apply texture-region/get-u2 args))

(defn get-v [& args]
  (apply texture-region/get-v args))

(defn get-v2 [& args]
  (apply texture-region/get-v2 args))

(defn new [& args]
  (apply texture-region/new args))
