(ns create.put-colors
  (:require [clojure.graphics.colors :as colors]))

(defn step [_ctx]
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]}))
