(ns moon.create.default-font
  (:require [gdl.context :as context]
            [gdl.files :as files]))

(defn do!
  [{:keys [ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font (context/generate-font ctx (files/internal files path) params)))
