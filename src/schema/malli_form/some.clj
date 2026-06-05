(ns schema.malli-form.some
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/some [_ _schemas]
  :some)
