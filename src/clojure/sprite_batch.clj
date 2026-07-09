(ns clojure.sprite-batch
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn new [& args]
  (apply sprite-batch/new args))
