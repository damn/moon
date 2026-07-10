#_(ns app-test
  (:require [gdl.backends.lwjgl :as lwjgl]))

#_(defn -main []
  (lwjgl/application!
   {:create! (fn [app])
    :dispose! (fn [])
    :render! (fn [])
    :resize! (fn [width height])
    :pause! (fn [])
    :resume! (fn [])
    :title "foo"
    :windowed-mode {:width 800 :height 600}
    :foreground-fps 60
    }
   )
  )
