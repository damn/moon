(ns clojure.val-max-validate
  (:require [clojure.schema :as schema]
            [clojure.malli-validate :refer [validate]]))

(defn f [val-max]
  (validate schema/v val-max))
