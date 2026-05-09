(ns moon.application.create.batch
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn step [ctx]
  (assoc ctx :ctx/batch (SpriteBatch.)))
