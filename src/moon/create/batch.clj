(ns moon.create.batch
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn do! [ctx]
  (assoc ctx :ctx/batch (SpriteBatch.)))
