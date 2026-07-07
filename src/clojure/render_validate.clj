(ns clojure.render-validate
  (:require [clojure.validate-humanize :refer [validate-humanize]]))

(defn step [ctx]
  (validate-humanize (:ctx/schema ctx) ctx)
  ctx)
