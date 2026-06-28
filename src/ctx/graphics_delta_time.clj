(ns ctx.graphics-delta-time
  (:import (com.badlogic.gdx Graphics)))

(defn graphics-delta-time
  [{:keys [ctx/graphics]}]
  (Graphics/.getDeltaTime graphics))
