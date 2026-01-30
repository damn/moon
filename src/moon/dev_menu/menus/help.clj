(ns moon.dev-menu.menus.help
  (:require [moon.input :as input]))

(defn create [_ctx]
  {:label "Help"
   :items [{:label input/info-text}]})
