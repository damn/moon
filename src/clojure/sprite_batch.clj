(ns clojure.sprite-batch
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn new []
  (SpriteBatch.))
