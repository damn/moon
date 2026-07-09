(ns clojure.texture-region
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn getRegionHeight [& args]
  (apply texture-region/getRegionHeight args))

(defn getRegionWidth [& args]
  (apply texture-region/getRegionWidth args))

(defn getTexture [& args]
  (apply texture-region/getTexture args))

(defn getU [& args]
  (apply texture-region/getU args))

(defn getU2 [& args]
  (apply texture-region/getU2 args))

(defn getV [& args]
  (apply texture-region/getV args))

(defn getV2 [& args]
  (apply texture-region/getV2 args))

(defn new [& args]
  (apply texture-region/new args))
