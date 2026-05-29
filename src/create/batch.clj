(ns create.batch)

(defn step [ctx]
  (assoc ctx :ctx/batch (com.badlogic.gdx.graphics.g2d.SpriteBatch.)))
