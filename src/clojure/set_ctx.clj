(ns clojure.set-ctx
  (:import (moon Stage)))

(defn f [^Stage stage ctx]
  (set! (.ctx stage) ctx))
