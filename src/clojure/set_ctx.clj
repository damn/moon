(ns clojure.set-ctx
  (:import (clojure Stage)))

(defn f [^Stage stage ctx]
  (set! (.ctx stage) ctx))
