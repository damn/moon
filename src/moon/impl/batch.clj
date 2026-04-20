(ns moon.impl.batch
  (:require [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn create [_ctx]
  (sprite-batch/create))
