(ns moon.schemas.malli-form.map
  (:require [moon.schemas.malli-form.create-map-schema :as create-map-schema]))

(defn f [[_ ks] schemas]
  (create-map-schema/f schemas ks))
