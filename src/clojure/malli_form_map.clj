(ns clojure.malli-form-map
  (:require [clojure.malli-form-create-map-schema :as create-map-schema]))

(defn f [[_ ks] schemas]
  (create-map-schema/f schemas ks))
