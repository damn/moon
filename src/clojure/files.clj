(ns clojure.files)

(defprotocol Files
  (internal [_ path]))
