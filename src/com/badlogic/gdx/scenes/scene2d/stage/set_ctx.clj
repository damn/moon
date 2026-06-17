(ns com.badlogic.gdx.scenes.scene2d.stage.set-ctx
  (:import (scene2d Stage)))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))
