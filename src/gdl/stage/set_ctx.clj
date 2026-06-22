(ns gdl.stage.set-ctx
  (:import (gdl Stage)))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))
