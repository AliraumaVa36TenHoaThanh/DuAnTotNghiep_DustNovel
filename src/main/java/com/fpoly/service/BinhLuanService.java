package com.fpoly.service;

import com.fpoly.model.BinhLuan;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.BinhLuanRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinhLuanService {

    private final BinhLuanRepository binhLuanRepo;
    private final TruyenRepository truyenRepo;
    private final NguoiDungRepository nguoiDungRepo;

    public BinhLuanService(BinhLuanRepository binhLuanRepo,
                           TruyenRepository truyenRepo,
                           NguoiDungRepository nguoiDungRepo) {
        this.binhLuanRepo = binhLuanRepo;
        this.truyenRepo = truyenRepo;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    // ================= LẤY COMMENT =================
    public List<BinhLuan> getByTruyen(Long truyenId) {
        return binhLuanRepo
                .findByTruyenIdOrderByNgayBinhLuanDesc(truyenId);
    }

    // ================= THÊM =================
    public void save(Long truyenId, Long userId, String noiDung) {

        Truyen truyen = truyenRepo.findById(truyenId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        NguoiDung user = nguoiDungRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        BinhLuan bl = new BinhLuan();
        bl.setTruyen(truyen);
        bl.setNguoiDung(user);
        bl.setNoiDung(noiDung);

        binhLuanRepo.save(bl);
    }

    // ================= REPLY =================
    public void reply(Long truyenId,
                      Long userId,
                      Long parentId,
                      String noiDung) {

        Truyen truyen = truyenRepo.findById(truyenId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        NguoiDung user = nguoiDungRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        BinhLuan parent = binhLuanRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy comment cha"));

        BinhLuan reply = new BinhLuan();
        reply.setTruyen(truyen);
        reply.setNguoiDung(user);
        reply.setNoiDung(noiDung);
        reply.setParent(parent);

        binhLuanRepo.save(reply);
    }

    // ================= UPDATE =================
    // Chỉ Admin hoặc Chủ comment được sửa
    public void update(Long commentId,
                       String noiDung,
                       Long currentUserId,
                       boolean isAdmin) {

        BinhLuan bl = binhLuanRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        Long ownerId = bl.getNguoiDung().getId();

        if (!ownerId.equals(currentUserId) && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này");
        }

        bl.setNoiDung(noiDung);
        binhLuanRepo.save(bl);
    }

    // ================= DELETE =================
    // Chủ comment, Admin, hoặc Người đăng truyện được xóa
    public void delete(Long commentId,
                       Long currentUserId,
                       boolean isAdmin) {

        BinhLuan bl = binhLuanRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        Long ownerId = bl.getNguoiDung().getId();
        Long uploaderId = bl.getTruyen().getNguoiDang().getId();

        if (!ownerId.equals(currentUserId)
                && !uploaderId.equals(currentUserId)
                && !isAdmin) {

            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }

        binhLuanRepo.delete(bl);
    }
}