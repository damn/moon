(ns clojure.gdx.stage.set-ctx
  (:import (scene2d Stage)))

(defn f [stage ctx]
  (set! (.ctx stage) ctx))
