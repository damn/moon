(ns gdl.context)

(defprotocol SpriteBatch
  (sprite-batch [_]))

(defprotocol FreeType
  (generate-font [_ file-handle params]))
