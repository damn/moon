(ns render.validate
  (:require [clojure.malli.validate-humanize :refer [validate-humanize]]))

(defn step [ctx]
  (validate-humanize (:ctx/schema ctx) ctx)
  ctx)
