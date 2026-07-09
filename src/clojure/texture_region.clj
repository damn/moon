(ns clojure.texture-region
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn get-region-height [& args]
  (apply texture-region/getRegionHeight args))

(defn get-region-width [& args]
  (apply texture-region/getRegionWidth args))

(defn get-texture [& args]
  (apply texture-region/getTexture args))

(defn get-u [& args]
  (apply texture-region/getU args))

(defn get-u2 [& args]
  (apply texture-region/getU2 args))

(defn get-v [& args]
  (apply texture-region/getV args))

(defn get-v2 [& args]
  (apply texture-region/getV2 args))

(defn new [& args]
  (apply texture-region/new args))
