(ns render.validate
  (:require [malli.utils.validate-humanize :refer [validate-humanize]]))

(defn step [ctx]
  (validate-humanize (:ctx/schema ctx) ctx)
  ctx)
