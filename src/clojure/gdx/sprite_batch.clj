(ns clojure.gdx.sprite-batch
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn create [_application]
  (sprite-batch/create))
