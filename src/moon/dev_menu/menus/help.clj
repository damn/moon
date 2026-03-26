(ns moon.dev-menu.menus.help)

(defn create [{:keys [ctx/controls-info]}]
  {:label "Help"
   :items [{:label controls-info}]})
