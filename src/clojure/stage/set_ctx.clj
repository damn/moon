(ns clojure.stage.set-ctx
  (:import (clojure Stage)))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))
