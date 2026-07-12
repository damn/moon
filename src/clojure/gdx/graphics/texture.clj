(ns clojure.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.texture :as texture]))

(defn new [texture-data]
  (texture/new texture-data))
