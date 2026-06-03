(ns clojure.gdx.scene2d.stage.set-ctx
  (:import (clojure.gdx.scene2d Stage)))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))
