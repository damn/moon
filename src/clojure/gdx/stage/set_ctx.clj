(ns clojure.gdx.stage.set-ctx
  (:import (moon Stage)))

(defn f [stage ctx]
  (set! (.ctx ^Stage stage) ctx))
